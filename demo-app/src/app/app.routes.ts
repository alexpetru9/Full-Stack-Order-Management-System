import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/login/login.component'),
  },
  {
    path: 'forgot-password',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/forgot-password/forgot-password').then((m) => m.ForgotPasswordComponent),
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/register/register').then((m) => m.RegisterComponent),
  },
  {
    path: 'people',
    canActivate: [authGuard, roleGuard(['ADMIN'])],
    loadComponent: () =>
      import('./features/person-list/person-list-page.component').then(
        (m) => m.PersonListPageComponent,
      ),
  },
  {
    path: 'products',
    canActivate: [authGuard, roleGuard(['ADMIN'])],
    loadComponent: () =>
      import('./features/product-list/product-list').then((m) => m.ProductListComponent),
  },
  {
    path: 'orders',
    canActivate: [authGuard, roleGuard(['ADMIN'])],
    loadComponent: () =>
      import('./features/order-list/order-list').then((m) => m.OrderListComponent),
  },
  {
    path: 'customer',
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
    loadComponent: () => import('./features/customer/customer').then((m) => m.CustomerComponent),
  },
  {
    path: 'error',
    loadComponent: () =>
      import('./features/not-found/not-found-page.component').then((m) => m.NotFoundPageComponent),
  },
  {
    path: '**',
    redirectTo: 'error',
  },
];
