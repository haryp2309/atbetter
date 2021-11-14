import React, {useRef} from 'react';
import {Card, Icon, Layout, Text} from '@ui-kitten/components';
import {StyleSheet, View} from 'react-native';
import {Screens} from '../../constants/screens';
import {NavigationStacks} from '../../constants/navigationStacks';

export type CardListItemProps = {
  label: string;
  iconName: string;
};

export const CardListItem: React.FC<CardListItemProps> = (
  props: CardListItemProps,
) => {
  const {label, iconName} = props;

  const ref = useRef<Icon<any>>(null);

  const handleClick = () => {
    ref.current ? ref.current.startAnimation() : false;
    setTimeout(() => {
      ref.current ? ref.current.stopAnimation() : false;
    }, 1000);
  };

  return (
    <Card style={styles.card} onPress={handleClick}>
      <View style={styles.cardContent}>
        <Icon
          name={iconName}
          style={styles.icon}
          fill="grey"
          animation="shake"
          animationConfig={{cycles: Infinity, useNativeDriver: true}}
          ref={ref}
        />
        <Text style={styles.text}>{label}</Text>
      </View>
    </Card>
  );
};

const styles = StyleSheet.create({
  card: {
    margin: 10,
    elevation: 4,
    borderWidth: 0,
    flexGrow: 1,
  },
  cardContent: {
    display: 'flex',
    flexWrap: 'nowrap',
    flexDirection: 'row',
  },
  icon: {width: 32, height: 32, marginRight: 20},
  text: {fontSize: 20},
});
