/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import React, {useEffect, useState} from 'react';
import {
  Dimensions,
  SafeAreaView,
  ScrollView,
  StatusBar,
  useColorScheme,
} from 'react-native';
import {
  ApplicationProvider,
  Button,
  IconRegistry,
  Layout,
  Text,
} from '@ui-kitten/components';
import * as eva from '@eva-design/eva';
import {Container} from './components/container';
import {AppBar} from './components/appBar';
import {EvaIconsPack} from '@ui-kitten/eva-icons';
import {CardItemContent, CardList} from './components/cardList';
import {subscribeToLocation} from './helpers/location.helpers';
import {API} from './api';

const App = () => {
  const darkMode = useColorScheme() === 'dark';

  const [listContent, setListContent] = useState<CardItemContent[]>([]);

  useEffect(() => {
    subscribeToLocation(async location => {
      const res = await API.getNearestStops(location);
      setListContent(
        res.map(({name}) => ({
          label: name,
        })),
      );
    });
  }, []);

  return (
    <>
      <IconRegistry icons={EvaIconsPack} />
      <ApplicationProvider {...eva} theme={darkMode ? eva.dark : eva.light}>
        <Layout style={{minHeight: Dimensions.get('window').height}}>
          <SafeAreaView>
            <StatusBar barStyle="default" />
            <AppBar />
            <ScrollView contentInsetAdjustmentBehavior="automatic">
              <Container>
                <CardList content={listContent} />
              </Container>
            </ScrollView>
          </SafeAreaView>
        </Layout>
      </ApplicationProvider>
    </>
  );
};

export default App;
