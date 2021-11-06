import React from 'react';
import {Layout} from '@ui-kitten/components';
import {CardListItem} from './cardListItem';
import {StyleSheet} from 'react-native';

export type CardListProps = {};

export const CardList: React.FC<CardListProps> = (props: CardListProps) => {
  const {} = props;
  return (
    <Layout style={styles.container}>
      {[
        {label: 'Gløshaugen'},
        {label: 'Høyskoleringen'},
        {label: 'Samfundet'},
      ].map(({label}) => (
        <CardListItem iconName="car" label={label} />
      ))}
    </Layout>
  );
};
const styles = StyleSheet.create({
  container: {
    flexDirection: 'column',
  },
  item: {},
});
