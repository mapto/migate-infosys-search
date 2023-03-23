import { ILocation } from 'app/shared/model/location.model';
import { IRole } from 'app/shared/model/role.model';
import { Country } from 'app/shared/model/enumerations/country.model';

export interface IInstitution {
  id?: number;
  name?: string;
  country?: Country | null;
  sites?: ILocation[] | null;
  position?: IRole | null;
}

export const defaultValue: Readonly<IInstitution> = {};
