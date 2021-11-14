import {createNativeStackNavigator} from '@react-navigation/native-stack';
import {StopInfo} from '../screens/stopInfo.screen';
import {StopsList} from '../screens/stopsList.screen';

const screens = {
  StopsList: {
    screen: StopsList,
  },
  StopInfo: {
    screen: StopInfo,
  },
};

const MainStack = createNativeStackNavigator();
