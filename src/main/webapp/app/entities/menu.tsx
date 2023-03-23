import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/person">
        Person
      </MenuItem>
      <MenuItem icon="asterisk" to="/institution">
        Institution
      </MenuItem>
      <MenuItem icon="asterisk" to="/work-group">
        Work Group
      </MenuItem>
      <MenuItem icon="asterisk" to="/work">
        Work
      </MenuItem>
      <MenuItem icon="asterisk" to="/role">
        Role
      </MenuItem>
      <MenuItem icon="asterisk" to="/location">
        Location
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
