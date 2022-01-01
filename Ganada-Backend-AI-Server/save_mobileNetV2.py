import numpy as np
from keras.models import load_model
from keras.preprocessing import image
from keras.applications.mobilenet_v2 import MobileNetV2, preprocess_input, decode_predictions
from keras.applications.nasnet import NASNetMobile, preprocess_input, decode_predictions
from keras.preprocessing import image
import os

def image_classification(img_path) :
	# cuda disable
	os.environ["CUDA_VISIBLE_DEVICES"] = "-1" 

# MobileNet model load
	model = MobileNetV2(weights='imagenet')
 
	img = image.load_img(img_path, target_size=(224, 224))
	x = image.img_to_array(img)
	x = np.expand_dims(x, axis=0)
	x = preprocess_input(x)
	
	preds = model.predict(x)

	pred_list = decode_predictions(preds, top=3)[0]
	print(pred_list)
	print('class name : ', pred_list[0][0])
	print('class_description : ', pred_list[0][1])
	print('score : ', pred_list[0][2])

	# https://gist.github.com/aaronpolhamus/964a4411c0906315deb9f4a3723aac57 
	# imageNet -> map_clsolc.txt

	if pred_list[0][0] == 'n03599486' :
		return '자전거'
	elif pred_list[0][0] == 'n02835271' :
		return '자전거'
	else :
		return "연필"
