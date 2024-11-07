import { Routes } from '@angular/router';
import { RegisterComponent } from './core/components/auth/register/register.component';
import { DashboardComponent } from './roles/candidat/stagiaire/components/dashboard/dashboard.component';
import { ApplicationsComponent } from './roles/candidat/shared-components/applications/applications.component';
import { DocumentsComponent } from './roles/candidat/stagiaire/components/documents/documents.component';
import { NotificationsComponent } from './roles/candidat/stagiaire/components/notifications/notifications.component';
import { ContractsComponent } from './roles/candidat/stagiaire/components/contracts/contracts.component';
import { AttestationsComponent } from './roles/candidat/stagiaire/components/attestations/attestations.component';
import { PaymentsComponent } from './roles/candidat/stagiaire/components/payments/payments.component';
import { LoginComponent } from './core/components/auth/login/login.component';
import { SuiviDemandeStageComponent } from './roles/candidat/shared-components/my-applications/my-applications.component';
import { DemandeStageComponent } from './roles/candidat/demandeur/components/demande-stage/demande-stage.component';



export const routes: Routes = [

    { 
      path: 'demande-stage',
      component: DemandeStageComponent
    },

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
        path: 'dashboard-stagiaire',
        component: DashboardComponent,
        title: 'Tableau de Bord'
    },

    {
        path: 'application',
        component: ApplicationsComponent,
        title: 'Renouvellement'
    },

    {
      path: 'my-applications',
      component: SuiviDemandeStageComponent,
      title: "Mes demandes"
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
