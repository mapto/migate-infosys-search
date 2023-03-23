import dayjs from 'dayjs';
import { IWorkGroup } from 'app/shared/model/work-group.model';
import { IRole } from 'app/shared/model/role.model';

export interface IWork {
  id?: number;
  name?: string;
  published?: string | null;
  collection?: IWorkGroup | null;
  responsibility?: IRole | null;
}

export const defaultValue: Readonly<IWork> = {};
