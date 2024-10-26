import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ApplicationsComponent } from './applications/applications.component';
import { DocumentsComponent } from './documents/documents.component';
import { NotificationsComponent } from './notifications/notifications.component';
import { ContractsComponent } from './contracts/contracts.component';
import { AttestationsComponent } from './attestations/attestations.component';
import { PaymentsComponent } from './payments/payments.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';

export const routes: Routes = [
    { 
      path: '', 
      redirectTo: 'login', 
      pathMatch: 'full' 
    },
    {
      path: 'login',
      component: LoginComponent
    },
    {
      path: 'register',
      component: RegisterComponent
    },
    {
        path: 'dashboard',
        component: DashboardComponent,
        title: 'Tableau de Bord'
      },
      {
        path: 'application',
        component: ApplicationsComponent,
        title: 'Renouvellement'
      },
      {
        path: 'documents',
        component: DocumentsComponent,
        title: 'Mes Documents'
      },
      { 
          path: 'notifications', 
          component: NotificationsComponent
      },
    { 
        path: 'documents/contracts', 
        component: ContractsComponent,
        title: 'Mes contrats'
    },

    { 
      path: 'documents/attestations', 
      component: AttestationsComponent,
      title: 'Mes attestations'
    },
    { 
      path: 'payments', 
      component: PaymentsComponent,
      title: 'Mes renumerations'
     },
];
