import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IsAuthorizedGuard } from './guard/is-authorized.guard';
import { LoginComponent } from './page/login/login.component';
import { NotificationComponent } from './page/notification/notification.component';
import { SignupComponent } from './page/signup/signup.component';
import { ThirdPartyNotificationComponent } from './page/third-party-notification/third-party-notification.component';

const routes: Routes = [
  { path: 'signup', component: SignupComponent },
  { path: 'login', component: LoginComponent },
  {
    path: 'notification',
    component: NotificationComponent,
    canActivate: [IsAuthorizedGuard],
  },
  {
    path: 'notification3rdp',
    component: ThirdPartyNotificationComponent,
    canActivate: [IsAuthorizedGuard],
  },
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
