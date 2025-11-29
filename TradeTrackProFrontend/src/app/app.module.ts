import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { TradeListComponent } from './components/trade-list/trade-list.component';
import { AddTradeComponent } from './components/add-trade/add-trade.component';
import { AnalyticsComponent } from './components/analytics/analytics.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { EditTradeComponent } from './components/edit-trade/edit-trade.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    TradeListComponent,
    AddTradeComponent,
    AnalyticsComponent,
    NavbarComponent,
    EditTradeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {}
