from azure.cognitiveservices.vision.computervision import ComputerVisionClient
from msrest.authentication import CognitiveServicesCredentials

from array import array
import os
from PIL import Image
import sys
import time
import logging
import configparser as parser


def make_caption(filename):
    ## Key & Endpoint    
    properties = parser.ConfigParser()
    properties.read('./config.ini')
    
    subscription_key = properties['AZURE']['subscription_key']
    endpoint = properties['AZURE']['endpoint']
    remote_image_url = "https://raw.githubusercontent.com/Azure-Samples/cognitive-services-sample-data-files/master/ComputerVision/Images/landmark.jpg"

    computervision_client = ComputerVisionClient(endpoint, CognitiveServicesCredentials(subscription_key))

    # ===== Describe an Image - local =====
    local_image = open(filename, "rb")

    # Call API
    description_result = computervision_client.describe_image_in_stream(local_image)

    # Get the captions (descriptions) from the response, with confidence level
    if (len(description_result.captions) == 0):
        logging.debug("No description detected.")
        return None
    else:
        for caption in description_result.captions:
            logging.debug("'{}' with confidence {:.2f}%".format(caption.text, caption.confidence * 100))
            return caption.text
    '''
    END - Describe an Image - local
'''
