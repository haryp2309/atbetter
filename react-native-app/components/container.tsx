import React, {ReactNode} from 'react';
import {Layout} from '@ui-kitten/components';
import {StyleSheet} from 'react-native';

type ContainerProps = {
  children: ReactNode | ReactNode[];
};

export const Container: React.FC<ContainerProps> = (props: ContainerProps) => {
  const {children} = props;
  return <Layout style={styles.layout1}>{children}</Layout>;
};

const styles = StyleSheet.create({
  layout1: {
    margin: 10,
  },
});
