import { IRole } from 'app/shared/model/role.model';
import { ILocation } from 'app/shared/model/location.model';
import { IWork } from 'app/shared/model/work.model';
import { Country } from 'app/shared/model/enumerations/country.model';

export interface IInstitution {
  id?: number;
  name?: string;
  country?: Country | null;
  institutionRoleNames?: IRole[] | null;
  site?: ILocation | null;
  products?: IWork[] | null;
}

export const defaultValue: Readonly<IInstitution> = {};
