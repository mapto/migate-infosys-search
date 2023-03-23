import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Person from './person';
import Institution from './institution';
import WorkGroup from './work-group';
import Work from './work';
import Role from './role';
import Location from './location';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="person/*" element={<Person />} />
        <Route path="institution/*" element={<Institution />} />
        <Route path="work-group/*" element={<WorkGroup />} />
        <Route path="work/*" element={<Work />} />
        <Route path="role/*" element={<Role />} />
        <Route path="location/*" element={<Location />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
