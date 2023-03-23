import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './role.reducer';

export const RoleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const roleEntity = useAppSelector(state => state.role.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="roleDetailsHeading">Role</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{roleEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{roleEntity.name}</dd>
          <dt>
            <span id="start">Start</span>
          </dt>
          <dd>{roleEntity.start ? <TextFormat value={roleEntity.start} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="end">End</span>
          </dt>
          <dd>{roleEntity.end ? <TextFormat value={roleEntity.end} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>Sponsor</dt>
          <dd>{roleEntity.sponsor ? roleEntity.sponsor.name : ''}</dd>
          <dt>Person</dt>
          <dd>{roleEntity.person ? roleEntity.person.name : ''}</dd>
          <dt>Product</dt>
          <dd>{roleEntity.product ? roleEntity.product.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/role" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/role/${roleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default RoleDetail;
