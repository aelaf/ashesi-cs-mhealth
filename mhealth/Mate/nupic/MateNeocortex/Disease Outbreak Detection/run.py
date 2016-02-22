    #This is the main client of the system

import csv
import datetime

from nupic.frameworks.opf.modelfactory import ModelFactory
from nupic.data.inference_shifter import InferenceShifter
import nupic_anomaly_output as nupic_output
from nupic.algorithms.anomaly_likelihood import AnomalyLikelihood

#import nupic_output

MODEL_PARAMS = \
{ 'aggregationInfo': { 'days': 0,
                       'fields': [],
                       'hours': 0,
                       'microseconds': 0,
                       'milliseconds': 0,
                       'minutes': 0,
                       'months': 0,
                       'seconds': 0,
                       'weeks': 0,
                       'years': 0},
  'model': 'CLA',
  'modelParams': { 'anomalyParams': { u'anomalyCacheRecords': None,
                                      u'autoDetectThreshold': None,
                                      u'autoDetectWaitRecords': None},
                   'clParams': { 'alpha': 0.0001,
                                 'clVerbosity': 0,
                                 'regionName': 'CLAClassifierRegion',
                                 'steps': '1'},
                   'inferenceType': 'TemporalAnomaly',
                   'sensorParams': { 'encoders': { '_classifierInput': { 'classifierOnly': True,
                                                                         'fieldname': 'vaccine_name',
                                                                         'n': 121,
                                                                         'name': '_classifierInput',
                                                                         'type': 'SDRCategoryEncoder',
                                                                         'w': 21},
                                                   u'vaccine_date_dayOfWeek': None,
                                                   u'vaccine_date_timeOfDay': None,
                                                   u'vaccine_date_weekend': None,
                                                   u'vaccine_name': { 'fieldname': 'vaccine_name',
                                                                      'n': 121,
                                                                      'name': 'vaccine_name',
                                                                      'type': 'SDRCategoryEncoder',
                                                                      'w': 21}},
                                     'sensorAutoReset': None,
                                     'verbosity': 0},
                   'spEnable': True,
                   'spParams': { 'columnCount': 2048,
                                 'globalInhibition': 1,
                                 'inputWidth': 0,
                                 'maxBoost': 2.0,
                                 'numActiveColumnsPerInhArea': 40,
                                 'potentialPct': 0.8,
                                 'seed': 1956,
                                 'spVerbosity': 0,
                                 'spatialImp': 'cpp',
                                 'synPermActiveInc': 0.05,
                                 'synPermConnected': 0.1,
                                 'synPermInactiveDec': 0.1},
                   'tpEnable': True,
                   'tpParams': { 'activationThreshold': 12,
                                 'cellsPerColumn': 32,
                                 'columnCount': 2048,
                                 'globalDecay': 0.0,
                                 'initialPerm': 0.21,
                                 'inputWidth': 2048,
                                 'maxAge': 0,
                                 'maxSegmentsPerCell': 128,
                                 'maxSynapsesPerSegment': 32,
                                 'minThreshold': 9,
                                 'newSynapseCount': 20,
                                 'outputType': 'normal',
                                 'pamLength': 1,
                                 'permanenceDec': 0.1,
                                 'permanenceInc': 0.1,
                                 'seed': 1960,
                                 'temporalImp': 'cpp',
                                 'verbosity': 0},
                   'trainSPNetOnlyIfRequested': False},
  'predictAheadTime': None,
  'version': 1}



DATE_FORMAT = "%Y-%m-%d %H:%M:%S"

def createModel():
    model = ModelFactory.create(MODEL_PARAMS)
    model.enableInference({
        "predictedField": "vaccine_name"
    })
    return model

def runModel(model, inputFilePath):
    inputFile = open(inputFilePath, "rb")
    csvReader = csv.reader(inputFile)
    # skip header rows
    csvReader.next()
    csvReader.next()
    csvReader.next()

    output = nupic_output.NuPICFileOutput("Vaccination")

    shifter = InferenceShifter()
    counter = 0
    actualCount = 0
    predictCount = 0
    miss = 0
    hit = 0

    for row in csvReader:
        counter += 1
        if(counter % 10 == 0):
            print "Read %i lines..." % counter
        vaccine_date = datetime.datetime.strptime(row[2], DATE_FORMAT)
        vaccine_name = str(row[1])
        result = model.run({
            "vaccine_date": vaccine_date,
            "vaccine_name": vaccine_name
        })

        prediction = result.inferences["multiStepBestPredictions"][1]
        result = shifter.shift(result)
        anomalyScore = result.inferences["anomalyScore"]
        #output.write([vaccine_date], [vaccine_name], [prediction])
        print len(vaccine_name)
        output.write(vaccine_date, vaccine_name, prediction, anomalyScore)
        if prediction == "Yellow Fever":
            predictCount += 1
        if vaccine_name == "Yellow Fever":
            actualCount += 1
        if vaccine_name == prediction:
            hit += 1
        else:
            miss += 1
        print counter, "community member_id: ", row[0], "Actual: ", vaccine_name, "Predicted: ", prediction, "------", anomalyScore
    print"\n Number of actuals: ", actualCount," \n Number of predictions: ", predictCount
    print "\n hits: ", hit,"\n misses: ", miss

def runHospitalModel(inputFilePath):
    model = createModel()
    runModel(model, inputFilePath)


if __name__ == "__main__":
    inputFilePath = "vaccine_record.csv"
    runHospitalModel(inputFilePath)
