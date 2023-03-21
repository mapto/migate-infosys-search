import dayjs from 'dayjs';
import { IInstitution } from 'app/shared/model/institution.model';
import { IRole } from 'app/shared/model/role.model';
import { IWorkGroup } from 'app/shared/model/work-group.model';
import { IPerson } from 'app/shared/model/person.model';

export interface IWork {
  id?: number;
  name?: string;
  published?: string | null;
  sponsors?: IInstitution[] | null;
  workRoleNames?: IRole[] | null;
  collections?: IWorkGroup[] | null;
  responsibles?: IPerson[] | null;
}

export const defaultValue: Readonly<IWork> = {};
