from hyperopt import fmin, tpe, hp
import time

max_loss = 1e50


# 自定义01分布
def binary_distribution(name):
    return hp.choice(name, [0, 1])


# Define the search space
space = {
    'length': hp.uniform('length', 32, 64),
    'AttrDistributeLow': hp.normal('AttrDistributeLow', 3, 3),
    'AttrDistributeHigh': hp.normal('AttrDistributeHigh', 3, 3),
    'ValueDistributeLow': hp.normal('ValueDistributeLow', 3, 3),
    'ValueDistributeHigh': hp.normal('ValueDistributeHigh', 3, 3),
    'TupleDistributeLow': hp.normal('TupleDistributeLow', 3, 3),
    'TupleDistributeHigh': hp.normal('TupleDistributeHigh', 3, 3),
    'dropSourceEdge': binary_distribution('dropSourceEdge'),
    'dropSampleEdge': binary_distribution('dropSampleEdge'),
    'isCBOW': binary_distribution('isCBOW'),
    'dim': hp.uniform('dim', 128, 256),
    'windowSize': hp.uniform('windowSize', 2, 7)
}


# Define the objective function
def objective(params):
    out = open("E:\\GitHub\\pythonProject\\log\\para_bayes\\output.txt", "w")
    out.write(str(params['length']) + "\n")
    out.write(str(params['AttrDistributeLow']) + "\n")
    out.write(str(params['AttrDistributeHigh']) + "\n")
    out.write(str(params['ValueDistributeLow']) + "\n")
    out.write(str(params['ValueDistributeHigh']) + "\n")
    out.write(str(params['TupleDistributeLow']) + "\n")
    out.write(str(params['TupleDistributeHigh']) + "\n")
    out.write(str(params['dropSourceEdge']) + "\n")
    out.write(str(params['dropSampleEdge']) + "\n")
    out.write(str(params['isCBOW']) + "\n")
    out.write(str(params['dim']) + "\n")
    out.write(str(params['windowSize']) + "\n")

    out.close()

    in_put = open("E:\\GitHub\\pythonProject\\log\\para_bayes\\input.txt", "r")
    loss = float(in_put.read())
    in_put.close()
    print("loss:", loss)
    return loss


# Use TPE algorithm to optimize the hyperparameters
def bop():
    start = time.time()
    best = fmin(fn=objective,
                space=space,
                algo=tpe.suggest,
                max_evals=10)
    print(best)
    end = time.time()
    print("total time : ", start - end, "(s)")


bop()
