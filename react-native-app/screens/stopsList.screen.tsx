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
import {Container} from '../components/container';
import {AppBar} from '../components/appBar';
import {EvaIconsPack} from '@ui-kitten/eva-icons';
import {CardItemContent, CardList} from '../components/cardList';
import {subscribeToLocation} from '../helpers/location.helpers';
import {API} from '../api';
import {Screens} from '../constants/screens';

export const StopsList = () => {
  const darkMode = useColorScheme() === 'dark';

  const [listContent, setListContent] = useState<CardItemContent[]>([]);

  useEffect(() => {
    subscribeToLocation(async location => {
      const res = await API.getNearestStops(location);
      setListContent(
        res.map(({name, id}) => ({
          label: name,
          id,
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
