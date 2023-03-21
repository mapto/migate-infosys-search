import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IWork } from 'app/shared/model/work.model';
import { getEntities as getWorks } from 'app/entities/work/work.reducer';
import { IWorkGroup } from 'app/shared/model/work-group.model';
import { getEntity, updateEntity, createEntity, reset } from './work-group.reducer';

export const WorkGroupUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const works = useAppSelector(state => state.work.entities);
  const workGroupEntity = useAppSelector(state => state.workGroup.entity);
  const loading = useAppSelector(state => state.workGroup.loading);
  const updating = useAppSelector(state => state.workGroup.updating);
  const updateSuccess = useAppSelector(state => state.workGroup.updateSuccess);

  const handleClose = () => {
    navigate('/work-group');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getWorks({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...workGroupEntity,
      ...values,
      work: works.find(it => it.id.toString() === values.work.toString()),
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
          ...workGroupEntity,
          work: workGroupEntity?.work?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="migateInfosysApp.workGroup.home.createOrEditLabel" data-cy="WorkGroupCreateUpdateHeading">
            Create or edit a Work Group
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="work-group-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="work-group-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField id="work-group-work" name="work" data-cy="work" label="Work" type="select">
                <option value="" key="0" />
                {works
                  ? works.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/work-group" replace color="info">
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

export default WorkGroupUpdate;
