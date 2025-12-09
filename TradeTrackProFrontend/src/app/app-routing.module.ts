import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { TradeListComponent } from './components/trade-list/trade-list.component';
import { AddTradeComponent } from './components/add-trade/add-trade.component';
import { AnalyticsComponent } from './components/analytics/analytics.component';
import { EditTradeComponent } from './components/edit-trade/edit-trade.component';
import { HomeComponent } from './components/home/home.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ProfileSettingsComponent } from './components/profile-settings/profile-settings.component';

import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'trades', component: TradeListComponent, canActivate: [AuthGuard] },
  { path: 'add-trade', component: AddTradeComponent, canActivate: [AuthGuard] },
  { path: 'edit-trade/:id', component: EditTradeComponent, canActivate: [AuthGuard] },
  { path: 'analytics', component: AnalyticsComponent, canActivate: [AuthGuard] },
  { path: 'profile', component: ProfileSettingsComponent, canActivate: [AuthGuard] },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
