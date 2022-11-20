import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './page/login/login.component';
import { SignupComponent } from './page/signup/signup.component';
import { NotificationComponent } from './page/notification/notification.component';
import { ApiInterceptor } from './interceptor/api.interceptor';
import { ThirdPartyNotificationComponent } from './page/third-party-notification/third-party-notification.component';
import { PublisherComponent } from './component/publisher/publisher.component';
import { SubscriberComponent } from './component/subscriber/subscriber.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SignupComponent,
    NotificationComponent,
    ThirdPartyNotificationComponent,
    PublisherComponent,
    SubscriberComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: ApiInterceptor,
    multi: true
}],
  bootstrap: [AppComponent]
})
export class AppModule { }
