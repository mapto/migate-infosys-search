import dayjs from 'dayjs';
import { IInstitution } from 'app/shared/model/institution.model';
import { IPerson } from 'app/shared/model/person.model';
import { IWork } from 'app/shared/model/work.model';

export interface IRole {
  id?: number;
  name?: string;
  start?: string | null;
  end?: string | null;
  sponsor?: IInstitution | null;
  person?: IPerson | null;
  product?: IWork | null;
}

export const defaultValue: Readonly<IRole> = {};
