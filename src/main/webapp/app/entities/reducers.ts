import person from 'app/entities/person/person.reducer';
import institution from 'app/entities/institution/institution.reducer';
import workGroup from 'app/entities/work-group/work-group.reducer';
import work from 'app/entities/work/work.reducer';
import role from 'app/entities/role/role.reducer';
import location from 'app/entities/location/location.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  person,
  institution,
  workGroup,
  work,
  role,
  location,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
