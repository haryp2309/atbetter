/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import React from 'react';
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
} from '@ui-kitten/components';
import * as eva from '@eva-design/eva';
import {Container} from './components/container';
import {AppBar} from './components/appBar';
import {EvaIconsPack} from '@ui-kitten/eva-icons';
import {CardList} from './components/cardList';

const App = () => {
  const darkMode = useColorScheme() === 'dark';
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
                <CardList />
              </Container>
            </ScrollView>
          </SafeAreaView>
        </Layout>
      </ApplicationProvider>
    </>
  );
};

export default App;
