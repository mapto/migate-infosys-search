import { IWork } from 'app/shared/model/work.model';

export interface IWorkGroup {
  id?: number;
  name?: string;
  works?: IWork[] | null;
}

export const defaultValue: Readonly<IWorkGroup> = {};
