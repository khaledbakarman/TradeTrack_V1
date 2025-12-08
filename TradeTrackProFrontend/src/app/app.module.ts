import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { NgChartsModule } from 'ng2-charts';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { TradeListComponent } from './components/trade-list/trade-list.component';
import { AddTradeComponent } from './components/add-trade/add-trade.component';
import { AnalyticsComponent } from './components/analytics/analytics.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { EditTradeComponent } from './components/edit-trade/edit-trade.component';
import { HomeComponent } from './components/home/home.component';
import { PerformanceCalendarComponent } from './components/performance-calendar/performance-calendar.component';

import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './auth/token.interceptor';
import { TradeCardComponent } from './components/trade-card/trade-card.component';
import { QuickStatsComponent } from './components/quick-stats/quick-stats.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    TradeListComponent,
    AddTradeComponent,
    AnalyticsComponent,
    NavbarComponent,
    EditTradeComponent,
    HomeComponent,
    TradeCardComponent,
    PerformanceCalendarComponent,
    QuickStatsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    NgChartsModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
