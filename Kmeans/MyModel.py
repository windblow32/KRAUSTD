import torch.nn as nn


class MyModel(nn.Module):
    def __init__(self):
        super(MyModel, self).__init__()
        self.net = nn.Sequential(
            nn.Linear(10, 32),
            nn.Sigmoid(),
            nn.Linear(32, 1)

        )

    def forward(self, x):
        return self.net(x)

    # params = model.parameters
    # torch.optim.SGD(params,lr,momentum = 0)
