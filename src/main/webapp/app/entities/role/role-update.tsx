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
import { IPerson } from 'app/shared/model/person.model';
import { getEntities as getPeople } from 'app/entities/person/person.reducer';
import { IWork } from 'app/shared/model/work.model';
import { getEntities as getWorks } from 'app/entities/work/work.reducer';
import { IRole } from 'app/shared/model/role.model';
import { getEntity, updateEntity, createEntity, reset } from './role.reducer';

export const RoleUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const institutions = useAppSelector(state => state.institution.entities);
  const people = useAppSelector(state => state.person.entities);
  const works = useAppSelector(state => state.work.entities);
  const roleEntity = useAppSelector(state => state.role.entity);
  const loading = useAppSelector(state => state.role.loading);
  const updating = useAppSelector(state => state.role.updating);
  const updateSuccess = useAppSelector(state => state.role.updateSuccess);

  const handleClose = () => {
    navigate('/role');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getInstitutions({}));
    dispatch(getPeople({}));
    dispatch(getWorks({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...roleEntity,
      ...values,
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
          ...roleEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="migateInfosysApp.role.home.createOrEditLabel" data-cy="RoleCreateUpdateHeading">
            Create or edit a Role
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="role-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="role-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField label="Start" id="role-start" name="start" data-cy="start" type="date" />
              <ValidatedField label="End" id="role-end" name="end" data-cy="end" type="date" />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/role" replace color="info">
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

export default RoleUpdate;
