export class NotificationModel {
  data: string;
  id: string;
  type: string;
  duration: number = 2000;
  constructor(data?: string, id?: string, type?: string) {
    this.data = data!;
    this.id = id!;
    this.type = type!;
  }
}
