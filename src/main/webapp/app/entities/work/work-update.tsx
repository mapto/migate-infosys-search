import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IInstitution } from 'app/shared/model/institution.model';
import { getEntities as getInstitutions } from 'app/entities/institution/institution.reducer';
import { IRole } from 'app/shared/model/role.model';
import { getEntities as getRoles } from 'app/entities/role/role.reducer';
import { IPerson } from 'app/shared/model/person.model';
import { getEntities as getPeople } from 'app/entities/person/person.reducer';
import { IWork } from 'app/shared/model/work.model';
import { getEntity, updateEntity, createEntity, reset } from './work.reducer';

export const WorkUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const institutions = useAppSelector(state => state.institution.entities);
  const roles = useAppSelector(state => state.role.entities);
  const people = useAppSelector(state => state.person.entities);
  const workEntity = useAppSelector(state => state.work.entity);
  const loading = useAppSelector(state => state.work.loading);
  const updating = useAppSelector(state => state.work.updating);
  const updateSuccess = useAppSelector(state => state.work.updateSuccess);

  const handleClose = () => {
    navigate('/work');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getInstitutions({}));
    dispatch(getRoles({}));
    dispatch(getPeople({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...workEntity,
      ...values,
      sponsors: mapIdList(values.sponsors),
      workRoleNames: mapIdList(values.workRoleNames),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...workEntity,
          sponsors: workEntity?.sponsors?.map(e => e.id.toString()),
          workRoleNames: workEntity?.workRoleNames?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="migateInfosysApp.work.home.createOrEditLabel" data-cy="WorkCreateUpdateHeading">
            Create or edit a Work
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="work-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="work-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField label="Published" id="work-published" name="published" data-cy="published" type="date" />
              <ValidatedField label="Sponsor" id="work-sponsor" data-cy="sponsor" type="select" multiple name="sponsors">
                <option value="" key="0" />
                {institutions
                  ? institutions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label="Work Role Name"
                id="work-workRoleName"
                data-cy="workRoleName"
                type="select"
                multiple
                name="workRoleNames"
              >
                <option value="" key="0" />
                {roles
                  ? roles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/work" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default WorkUpdate;
