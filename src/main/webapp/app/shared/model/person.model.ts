import dayjs from 'dayjs';
import { IRole } from 'app/shared/model/role.model';
import { Gender } from 'app/shared/model/enumerations/gender.model';
import { Country } from 'app/shared/model/enumerations/country.model';
import { Language } from 'app/shared/model/enumerations/language.model';

export interface IPerson {
  id?: number;
  name?: string;
  gender?: Gender | null;
  dob?: string | null;
  dod?: string | null;
  country?: Country | null;
  language?: Language | null;
  job?: IRole | null;
}

export const defaultValue: Readonly<IPerson> = {};
