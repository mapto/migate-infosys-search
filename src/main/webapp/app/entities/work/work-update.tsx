import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IWorkGroup } from 'app/shared/model/work-group.model';
import { getEntities as getWorkGroups } from 'app/entities/work-group/work-group.reducer';
import { IWork } from 'app/shared/model/work.model';
import { getEntity, updateEntity, createEntity, reset } from './work.reducer';

export const WorkUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const workGroups = useAppSelector(state => state.workGroup.entities);
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

    dispatch(getWorkGroups({}));
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
      collection: workGroups.find(it => it.id.toString() === values.collection.toString()),
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
          collection: workEntity?.collection?.id,
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
              <ValidatedField id="work-collection" name="collection" data-cy="collection" label="Collection" type="select">
                <option value="" key="0" />
                {workGroups
                  ? workGroups.map(otherEntity => (
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
