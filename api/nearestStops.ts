import {BusStation} from '../typings/busStation';
import {Location} from '../typings/location';
import {enturClient} from './enturClient';

export const getNearestStops = async (location: Location) => {
  const res = await enturClient.getStopPlacesByPosition(location);

  return res.map(
    ({name, id}): BusStation => ({
      id,
      name,
    }),
  );
};
