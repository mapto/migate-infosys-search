import dayjs from 'dayjs';
import { IInstitution } from 'app/shared/model/institution.model';
import { IPerson } from 'app/shared/model/person.model';
import { IWork } from 'app/shared/model/work.model';

export interface IRole {
  id?: number;
  name?: string;
  start?: string | null;
  end?: string | null;
  institutionNames?: IInstitution[] | null;
  people?: IPerson[] | null;
  workNames?: IWork[] | null;
}

export const defaultValue: Readonly<IRole> = {};
