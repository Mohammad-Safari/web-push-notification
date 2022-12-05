import { Observable, Observer, Subject, timer } from 'rxjs';
import { distinctUntilChanged, share, takeWhile } from 'rxjs/operators';
import { WebSocketSubject, WebSocketSubjectConfig } from 'rxjs/webSocket';

class AutoReconnectWebsocketSubject<T> extends Subject<T> {
  private reconnectionObservable: Observable<number>;
  private wsSubjectConfig: WebSocketSubjectConfig<T>;
  private socket: WebSocketSubject<T>;
  private connectionObserver: Observer<boolean>;
  public connectionStatus: Observable<unknown>;

  constructor(
    private url: string,
    private options?: WebSocketSubjectConfig<T> & {
      reconnectInterval: number;
      reconnectAttempts: number;
    }
  ) {
    super();
    this.connectionStatus = new Observable((observer) => {
      this.connectionObserver = observer;
    }).pipe(share(), distinctUntilChanged());

    this.wsSubjectConfig = {
      url: url,
      closeObserver: {
        next: (e: CloseEvent) => {
          this.socket = null as unknown as WebSocketSubject<T>;
          this.connectionObserver.next(false);
        },
      },
      openObserver: {
        next: (e: Event) => {
          this.connectionObserver.next(true);
        },
      },
    };
    this.connect();
    this.connectionStatus.subscribe((isConnected) => {
      if (
        !this.reconnectionObservable &&
        typeof isConnected == 'boolean' &&
        !isConnected
      ) {
        this.reconnect();
      }
    });
  }

  connect(): void {
    this.socket = new WebSocketSubject(this.wsSubjectConfig);
    this.socket.subscribe(
      (m) => {
        this.next(m);
      },
      (error: Event) => {
        if (!this.socket) {
          this.reconnect();
        }
      }
    );
  }

  reconnect(): void {
    this.reconnectionObservable = timer(
      this.options?.reconnectInterval ?? 1000
    ).pipe(
      takeWhile((_v, index) => {
        return index < (this.options?.reconnectAttempts ?? 10) && !this.socket;
      })
    );
    this.reconnectionObservable.subscribe({
      next: () => {
        this.connect();
      },
      complete: () => {
        this.reconnectionObservable = null as unknown as Observable<number>;
        if (!this.socket) {
          this.complete();
          this.connectionObserver.complete();
        }
      },
    });
  }
}
