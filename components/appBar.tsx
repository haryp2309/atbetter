import React from 'react';
import {Text, TopNavigation} from '@ui-kitten/components';
import {StyleSheet} from 'react-native';

export type AppBarProps = {};

export const AppBar: React.FC<AppBarProps> = (props: AppBarProps) => {
  return (
    <TopNavigation
      title={evaProps => (
        <Text {...evaProps} style={styles.text}>
          ATBetter
        </Text>
      )}
      style={styles.bar}
    />
  );
};

const styles = StyleSheet.create({
  bar: {
    shadowColor: '#000000',
    elevation: 10,
    height: 70,
  },
  text: {
    padding: 10,
    fontSize: 25,
  },
});
