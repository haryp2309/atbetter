import React from 'react';
import {Layout} from '@ui-kitten/components';
import {CardListItem} from './cardListItem';
import {StyleSheet} from 'react-native';

export type CardItemContent = {
  label: string;
};

export type CardListProps = {
  content: CardItemContent[];
};

export const CardList: React.FC<CardListProps> = (props: CardListProps) => {
  const {content} = props;
  return (
    <Layout style={styles.container}>
      {content.map(({label}) => (
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
