import React from "react";
import { NativeModules, View, Image, NativeEventEmitter } from "react-native";
import PropTypes from "prop-types";

function usePrevious(value) {
  const ref = React.useRef();
  React.useEffect(() => {
    ref.current = value;
  });
  return ref.current;
}

const ReactNativePickerModule = ({
  value,
  items,
  images,
  title,
  onValueChange,
  pickerRef,
  onCancel,
  onDismiss,
  onDelete
}) => {
  const [isShown, setIsShown] = React.useState(false);
  const prevItems = usePrevious(items);
  React.useEffect(() => {
    const eventEmitter = new NativeEventEmitter(
      NativeModules.ReactNativePickerModule
    );
    const callback = event => {
      onDelete(event.item, event.position);
    };
    eventEmitter.addListener("ItemDeleted", callback);
    return () => {
      eventEmitter.removeListener("ItemDeleted", callback);
    };
  }, [onDelete]);
  const imageSet = images.map(img => Image.resolveAssetSource(img));
  const show = React.useCallback(() => {
    setIsShown(true);
    NativeModules.ReactNativePickerModule.show(
      items,
      imageSet,
      value,
      title,
      Boolean(onDelete),
      (val, idx) => {
        onValueChange(val, idx);
        onDismiss();
        setIsShown(false);
      },
      () => {
        onCancel();
        setIsShown(false);
      }
    );
  }, [
    imageSet,
    items,
    onCancel,
    onDelete,
    onDismiss,
    onValueChange,
    title,
    value
  ]);

  React.useEffect(() => {
    pickerRef({
      show
    });
  }, [pickerRef, show]);

  React.useEffect(() => {
    // re-create dialog if items change
    if (isShown && JSON.stringify(items) !== JSON.stringify(prevItems)) {
      NativeModules.ReactNativePickerModule.hide();
      if (items && items.length > 0) {
        show();
      } else {
        // all items deleted, cancel dialog
        onCancel();
        setIsShown(false);
      }
    }
  }, [pickerRef, items, isShown, prevItems, show, onCancel]);
  return <View style={{ display: "none" }} />;
};
ReactNativePickerModule.propTypes = {
  value: PropTypes.number,
  items: PropTypes.array.isRequired,
  images: PropTypes.array,
  title: PropTypes.string,
  onValueChange: PropTypes.func.isRequired,
  onCancel: PropTypes.func,
  onDismiss: PropTypes.func,
  onDelete: PropTypes.func,
  pickerRef: PropTypes.func.isRequired
};
ReactNativePickerModule.defaultProps = {
  value: -1,
  images: [],
  onCancel: () => {},
  onDismiss: () => {}
};
export default ReactNativePickerModule;
