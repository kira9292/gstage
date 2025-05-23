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
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';
import { NonAuthGuard } from './core/guards/non-auth.guard';
import { DashboardGwteComponent } from './roles/gwte/components/dashboard-gwte/dashboard-gwte.component';
import { PoposerManagerComponent } from './roles/gwte/components/poposer-manager/poposer-manager.component';
import { DashboardManagerComponent } from './roles/manager/components/dashboard-manager/dashboard-manager.component';
import { AdminRegisterComponent } from './roles/admin/components/admin-register/admin-register.component';
import {DashboardAdminComponent} from "./roles/admin/components/dashboard-admin/dashboard-admin.component";
import { StatsComponent } from './roles/gwte/components/stats/stats.component';



export const routes: Routes = [
    {
      path: 'demande-stage',
      component: DemandeStageComponent,
      canActivate: [NonAuthGuard]
    },
    {
      path: '',
      redirectTo: 'demande-stage',
      pathMatch: 'full'
    },
    {
      path: 'login',
      component: LoginComponent,
      canActivate: [NonAuthGuard]
    },
    {
      path: 'register',
      component: RegisterComponent,
    },
    {
      path: 'admin-register',
      component: AdminRegisterComponent
    },
    {
        path: 'dashboard-stagiaire',
        component: DocumentsComponent,
        title: 'Tableau de Bord',
        canActivate: [AuthGuard, RoleGuard],
        data: {expectedRole: 'ROLE_STAGIAIRE'}
    },


    {
      path: 'notifications',
      component: NotificationsComponent,
      canActivate: [AuthGuard, RoleGuard],
      data: {expectedRole: 'ROLE_STAGIAIRE'}
    },

    {
      path: 'documents/contracts',
      component: ContractsComponent,
      title: 'Mes contrats',
      canActivate: [AuthGuard, RoleGuard],
      data: {expectedRole: 'ROLE_STAGIAIRE'}
    },

    {
      path: 'documents/attestations',
      component: AttestationsComponent,
      title: 'Mes attestations',
      canActivate: [AuthGuard, RoleGuard],
      data: {expectedRole: 'ROLE_STAGIAIRE'}
    },


  {
    path: 'application',
    component: ApplicationsComponent
  },

    {
      path: 'payments',
      component: PaymentsComponent,
      title: 'Mes renumerations',
      canActivate: [AuthGuard, RoleGuard],
      data: {expectedRole: 'ROLE_STAGIAIRE'}
     },

     {
      path: 'dashboard-gwte',
      component: DashboardGwteComponent,
      canActivate: [AuthGuard, RoleGuard],
      data: {expectedRole: 'ROLE_ASSISTANT_GWTE'}
     },

     {
      path: 'propose-to-manager',
      component: PoposerManagerComponent,
      canActivate: [AuthGuard, RoleGuard],
      data: {expectedRole: 'ROLE_ASSISTANT_GWTE'}
     },

     {
      path: 'statistics',
      component: StatsComponent,
     },

     {
      path: 'dashboard-manager',
      component: DashboardManagerComponent,
      canActivate: [AuthGuard, RoleGuard],
      data: {expectedRole: 'ROLE_MANAGER'}
     },

    {
      path:'admin-register',
      component: AdminRegisterComponent,
      canActivate: [AuthGuard, RoleGuard],
      data: {expectedRole: 'ROLE_ADMIN'}
    },
  {
    path:'admin-dashboard',
    component:DashboardAdminComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: {expectedRole: 'ROLE_ADMIN'}
  }
];
