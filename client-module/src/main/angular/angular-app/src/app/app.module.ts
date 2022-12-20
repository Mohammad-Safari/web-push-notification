import { OverlayModule } from '@angular/cdk/overlay';
import { PortalModule } from '@angular/cdk/portal';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AutocompleteComponent } from './component/auto-complete/autocomplete.component';
import { OptionComponent } from './component/option/option.component';
import { PublisherComponent } from './component/publisher/publisher.component';
import { SubscriberComponent } from './component/subscriber/subscriber.component';
import { AutocompleteContentDirective } from './directive/autocomplete-content.directive';
import { AutocompleteDirective } from './directive/autocomplete.directive';
import { ApiInterceptor } from './interceptor/api.interceptor';
import { LoginComponent } from './page/login/login.component';
import { NotificationComponent } from './page/notification/notification.component';
import { SignupComponent } from './page/signup/signup.component';
import { SocketMessagingComponent } from './page/socket-messaging/socket-messaging.component';
import { ThirdPartyNotificationComponent } from './page/third-party-notification/third-party-notification.component';
import { FilterPipe } from './pipe/filter.pipe';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SignupComponent,
    NotificationComponent,
    ThirdPartyNotificationComponent,
    PublisherComponent,
    SubscriberComponent,
    OptionComponent,
    AutocompleteComponent,
    AutocompleteContentDirective,
    AutocompleteDirective,
    FilterPipe,
    SocketMessagingComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    OverlayModule,
    PortalModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ApiInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
