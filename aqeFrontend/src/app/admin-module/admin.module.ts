import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AdminComponent, DialogOverviewExampleDialog } from './admin/admin.component';
import { CreatedPageComponent } from './created-page/created-page.component';
import { HttpClientModule } from '@angular/common/http';
import { AdminServiceService } from './service/admin-service.service';
import { CoreModule } from '../core/core.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CreateModalComponent } from './create-modal/create-modal.component';
import { CreateuserComponent } from './createuser/createuser.component';
import { NgxSpinnerModule } from "ngx-spinner";

@NgModule({
  declarations: [
    AppComponent,
    AdminComponent,
    CreatedPageComponent,
    DashboardComponent,
    CreateuserComponent,
    CreateModalComponent,
    DialogOverviewExampleDialog
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    CoreModule,
    NgxSpinnerModule
    
  ],
  providers: [
    AdminServiceService
  ],
  entryComponents: [CreateModalComponent,DialogOverviewExampleDialog],
  bootstrap: [AppComponent]

})
export class AdminModule { }
