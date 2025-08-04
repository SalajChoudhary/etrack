import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/auth/auth.guard';
import { AutoLoginComponent } from './core/auth/auto-login.component';
import { AuthCallbackComponent } from './core/auth/auth-callback.component';
import { PageNotFoundComponent } from './core/auth/page-not-found/page-not-found.component';
import { UnauthorizedComponent } from './core/auth/unauthorized/unauthorized.component';

const routes: Routes = [
  { path: 'login', component: AutoLoginComponent },
  { path: 'callback', component: AuthCallbackComponent },
  { path: 'notFound', component: PageNotFoundComponent },
  { path: 'unauthorized', component: UnauthorizedComponent },
  {
    path: '',
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('./@modules/documents/documents.module').then(
        m => m.DocumentsModule
      )
  },
  { path: '**', redirectTo: 'notFound', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  providers: [],
  exports: [RouterModule]
})
export class AppRoutingModule { }
