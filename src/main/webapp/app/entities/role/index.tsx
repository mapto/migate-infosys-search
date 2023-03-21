import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Role from './role';
import RoleDetail from './role-detail';
import RoleUpdate from './role-update';
import RoleDeleteDialog from './role-delete-dialog';

const RoleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Role />} />
    <Route path="new" element={<RoleUpdate />} />
    <Route path=":id">
      <Route index element={<RoleDetail />} />
      <Route path="edit" element={<RoleUpdate />} />
      <Route path="delete" element={<RoleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RoleRoutes;
