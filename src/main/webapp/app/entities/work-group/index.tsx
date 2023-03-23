import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import WorkGroup from './work-group';
import WorkGroupDetail from './work-group-detail';
import WorkGroupUpdate from './work-group-update';
import WorkGroupDeleteDialog from './work-group-delete-dialog';

const WorkGroupRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<WorkGroup />} />
    <Route path="new" element={<WorkGroupUpdate />} />
    <Route path=":id">
      <Route index element={<WorkGroupDetail />} />
      <Route path="edit" element={<WorkGroupUpdate />} />
      <Route path="delete" element={<WorkGroupDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default WorkGroupRoutes;
