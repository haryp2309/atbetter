import RNLocation from 'react-native-location';
import {Location} from '../typings/location';

export const subscribeToLocation = async (
  callback: (location: Location) => void,
) => {
  const permissionGranted = await RNLocation.requestPermission({
    ios: 'whenInUse',
    android: {
      detail: 'coarse',
    },
  });
  if (permissionGranted) {
    return RNLocation.subscribeToLocationUpdates(locations => {
      const {latitude, longitude} = locations[0];
      const location: Location = {latitude, longitude};
      callback(location);
    });
  } else {
    return false;
  }
};

RNLocation.configure({
  distanceFilter: 5.0,
});
