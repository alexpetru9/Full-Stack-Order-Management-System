import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { LoginStore } from '../features/login/login.store';


export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return () => {
    const loginStore = inject(LoginStore);
    const router = inject(Router);

    const userRole = loginStore.role();

    if (userRole && allowedRoles.includes(userRole)) {
      return true;
    }

    if (userRole === 'ADMIN') {
      return router.parseUrl('/people');
    } else if (userRole === 'CUSTOMER') {
      return router.parseUrl('/customer');
    } else {
      return router.parseUrl('/login');
    }
  };
};
