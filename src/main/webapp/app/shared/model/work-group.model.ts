import { IWork } from 'app/shared/model/work.model';

export interface IWorkGroup {
  id?: number;
  name?: string;
  work?: IWork | null;
}

export const defaultValue: Readonly<IWorkGroup> = {};
