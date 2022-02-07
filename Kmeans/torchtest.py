import torch
import torch.nn as nn
from torch.utils.data import DataLoader
import numpy as np
# float required
x = torch.tensor([[1., -1.], [-1., 1.]], requires_grad=True)
z = x.pow(2).sum()
z.backward()  # calc微分
print(x.grad)

dataset = MyDataSet(file)
tr_set = DataLoader(dataset,16,shuffle=True)
model = MyModel().to(device)
criterion = nn.MSELoss()
optimizer = torch.optim.SGD(model.parameters(),0.1)
n_epochs = 10
for epoch in range(n_epochs):
    model.train()
    for x, y in tr_set:
        optimizer.zero_grad()
        x, y = x.to(device), y.to(device)
        pred = model(x)
        loss = criterion(pred, y)
        loss.backward()
        optimizer.step()
    model.eval()
    total_loss = 0;
    for x, y in dv_set:
        x, y = x.to(device), y.to(device)
        with torch.no_grad():
            pred = model(x)
            loss = criterion(pred,y)
            total_loss += loss.cpu().item() * len(x)
            avg_loss = total_loss / len(dv_set.dataset)

    model.eval()
    pred = []
    for x in tt_set:
        x = x.to(device)
        with torch.no_grad():
            pred = model(x)
            preds.append(pred.cpu())

    torch.save(model.state_dict(),path)
    ckpt = torch.load(path)
    model.load_state_dict(ckpt)