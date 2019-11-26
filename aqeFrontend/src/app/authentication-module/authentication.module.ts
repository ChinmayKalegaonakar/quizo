import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { EmployeeComponent } from './employee/employee.component';
import { HttpClientModule } from '@angular/common/http';
import { AddEmployeeComponent } from './add-employee/add-employee.component';
import { FormsModule } from '@angular/forms';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../material/material.module';
import { RegistrationComponent } from './registration/registration.component';
import { ToastrService, ToastrModule } from 'ngx-toastr';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';

@NgModule({
  declarations: [
    AppComponent,
    EmployeeComponent,
    AddEmployeeComponent,
    LoginComponent,
    LogoutComponent,
    RegistrationComponent,
    HeaderComponent,
    FooterComponent
  
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    MaterialModule,
    FormsModule,
    CommonModule,
    ToastrModule.forRoot({
      timeOut: 10000,
      positionClass:'toast-top-right',
      preventDuplicates: true,
    }),

  ],
  exports:[HeaderComponent],
  providers: [ ToastrService ],
  bootstrap: [AppComponent]
})
export class AuthenticationModule { }
