    #This is the main client of the system

import csv
import datetime
import nupic.frameworks.opf.model as brain

from nupic.frameworks.opf.modelfactory import ModelFactory
from nupic.data.inference_shifter import InferenceShifter
import nupic_anomaly_output as nupic_output
from model_params.vaccine_record_model_params import MODEL_PARAMS

mind_palaces = "mind_palaces/"

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

    output = nupic_output.NuPICPlotOutput("Vaccination")

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

        result = shifter.shift(result)
        prediction = result.inferences["multiStepBestPredictions"][1]

        anomalyScore = result.inferences["anomalyScore"]
        #output.write([vaccine_date], [vaccine_name], [prediction])
        print len(vaccine_name)
        output.write(vaccine_date, len(vaccine_name), len(prediction), anomalyScore)
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
    mind_palace = brain.Model
    mind_palace.save(mind_palaces)


if __name__ == "__main__":
    inputFilePath = "vaccine_record.csv"
    runHospitalModel(inputFilePath)
