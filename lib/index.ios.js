import React, { useEffect, useState } from "react"
import { StyleSheet, Text, TouchableOpacity, View } from "react-native"
import { Picker } from "@react-native-picker/picker"
import Modal from "react-native-modal"

const ReactNativePickerModule = ({
  pickerRef,
  value,
  items,
  title,
  onValueChange,
  cancelButton,
  confirmButton,
  onCancel,
  deleteButton,
  onDelete,
  contentContainerStyle,
  titleStyle,
  itemStyle,
  useNativeDriver,
  confirmButtonDisabledTextStyle,
  confirmButtonEnabledTextStyle,
  cancelButtonTextStyle,
  backdropColor,
  backdropOpacity,
  selectedColor,
  confirmButtonStyle,
  cancelButtonStyle,
}) => {
  const dismissPress = () => {
    setIsVisible(false)
    if (onCancel) {
      onCancel()
    }
  }
  const initialValue = !value ? (typeof items[0] === "object" ? items[0].value : items[0]) : value
  const [isVisible, setIsVisible] = useState(false)
  const [selectedValue, setSelectedValue] = useState(initialValue)
  useEffect(() => {
    pickerRef.current = {
      show: () => setIsVisible(true),
      hide: dismissPress,
    }
  })
  useEffect(() => {
    // reset selected value state when items change
    setSelectedValue(initialValue)
  }, [initialValue, items])
  return (
    <Modal
      backdropColor={backdropColor}
      backdropOpacity={backdropOpacity}
      onBackdropPress={dismissPress}
      onBackButtonPress={dismissPress}
      useNativeDriver={useNativeDriver}
      isVisible={isVisible}
      style={{ justifyContent: "flex-end" }}
      hideModalContentWhileAnimating={true}>
      <View style={[styles.content, contentContainerStyle]}>
        <View style={styles.titleView}>
          <Text style={[styles.titleText, titleStyle]}>{title}</Text>
        </View>
        <Picker
          itemStyle={itemStyle}
          selectedValue={selectedValue}
          style={{ maxHeight: 200, overflow: "hidden" }}
          onValueChange={setSelectedValue}>
          {items.map((item, index) => {
            if (item.hasOwnProperty("value") && item.hasOwnProperty("label")) {
              return (
                <Picker.Item
                  color={value === item.value ? selectedColor : undefined}
                  key={"item-" + index}
                  label={item.label.toString()}
                  value={item.value.toString()}
                />
              )
            } else {
              return (
                <Picker.Item
                  color={value === item ? selectedColor : undefined}
                  key={"item-" + index}
                  label={item.toString()}
                  value={item.toString()}
                />
              )
            }
          })}
        </Picker>
        {onDelete && (
          <TouchableOpacity
            activeOpacity={0.9}
            disabled={value === selectedValue}
            onPress={() => {
              onDelete(selectedValue)
            }}
            style={[styles.deleteButtonView]}>
            <Text style={[styles.deleteButtonText]}>{deleteButton}</Text>
          </TouchableOpacity>
        )}
        <TouchableOpacity
          activeOpacity={0.9}
          disabled={value === selectedValue}
          onPress={() => {
            onValueChange(selectedValue)
            setIsVisible(false)
          }}
          style={[styles.confirmButtonView, confirmButtonStyle]}>
          <Text
            style={[
              styles.confirmButtonText,
              selectedValue === value ? confirmButtonDisabledTextStyle : confirmButtonEnabledTextStyle,
            ]}>
            {confirmButton}
          </Text>
        </TouchableOpacity>
      </View>
      <View style={styles.cancelButton}>
        <TouchableOpacity
          activeOpacity={0.9}
          style={[styles.cancelButtonView, cancelButtonStyle]}
          onPress={dismissPress}>
          <Text style={[styles.cancelButtonText, cancelButtonTextStyle]}>{cancelButton}</Text>
        </TouchableOpacity>
      </View>
    </Modal>
  )
}

const styles = StyleSheet.create({
  content: {
    backgroundColor: "white",
    borderRadius: 10,
    borderColor: "rgba(0, 0, 0, 0.1)",
  },
  confirmButtonView: {
    borderBottomEndRadius: 10,
    borderBottomStartRadius: 10,
    backgroundColor: "#FFF",
    borderTopWidth: 1,
    borderTopColor: "rgba(165,165,165,0.2)",
    paddingVertical: 15,
  },
  deleteButtonView: {
    borderBottomEndRadius: 10,
    borderBottomStartRadius: 10,
    backgroundColor: "#FFF",
    borderTopWidth: 1,
    borderTopColor: "rgba(165,165,165,0.2)",
    paddingVertical: 15,
  },
  confirmButtonText: {
    fontWeight: "500",
    fontSize: 18,
    textAlign: "center",
  },
  deleteButtonText: {
    fontWeight: "500",
    fontSize: 18,
    textAlign: "center",
    color: "rgba(255,0,0,1)",
  },
  cancelButton: {
    marginVertical: 10,
  },
  cancelButtonView: {
    backgroundColor: "#FFF",
    padding: 15,
    borderRadius: 10,
  },
  cancelButtonText: {
    fontWeight: "bold",
    fontSize: 18,
    textAlign: "center",
    color: "rgba(0,122,255,1)",
  },
  titleView: {
    padding: 12,
    borderBottomWidth: 1,
    borderBottomColor: "rgba(165,165,165,0.2)",
  },
  titleText: {
    fontWeight: "500",
    fontSize: 14,
    textAlign: "center",
    color: "#bdbdbd",
  },
})

ReactNativePickerModule.defaultProps = {
  confirmButtonEnabledTextStyle: {
    color: "rgba(0,122,255,1)",
  },
  confirmButtonDisabledTextStyle: {
    color: "rgba(0,0,0,0.2)",
  },
  cancelButton: "Cancel",
  confirmButton: "Confirm",
  deleteButton: "Delete",
  useNativeDriver: true,
}

export default ReactNativePickerModule
