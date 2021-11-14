import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import React, {useEffect, useState} from 'react';
import {NavigationStacks} from './constants/navigationStacks';
import {Screens} from './constants/screens';
import {StopsList} from './screens/stopsList.screen';

const MainStack = createNativeStackNavigator();

const App = () => {
  return (
    <NavigationContainer>
      <MainStack.Navigator initialRouteName={Screens.STOPS_LIST}>
        <MainStack.Screen name={Screens.STOPS_LIST}>
          {props => <StopsList />}
        </MainStack.Screen>
      </MainStack.Navigator>
    </NavigationContainer>
  );
};

export default App;
