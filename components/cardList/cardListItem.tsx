import React, {useRef} from 'react';
import {Card, Icon, Layout, Text} from '@ui-kitten/components';
import {StyleSheet} from 'react-native';

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
      <Layout style={styles.cardContent}>
        <Icon
          name={iconName}
          style={styles.icon}
          fill="grey"
          animation="shake"
          animationConfig={{cycles: Infinity, useNativeDriver: true}}
          ref={ref}
        />
        <Text style={styles.text}>{label}</Text>
      </Layout>
    </Card>
  );
};

const styles = StyleSheet.create({
  card: {
    margin: 10,
    elevation: 4,
    borderColor: 'white',
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
