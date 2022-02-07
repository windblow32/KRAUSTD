import torch.nn
from torch.utils.data import DataLoader, Dataset


class MyDataSet(Dataset):
   def __init__(self,file):
       # read from file
       self.data =
    def __getitem__(self, index):
        return self.data[index]
    def __len__(self):
        return len(self.data)
    dataset = MyDataSet(file)
    dataloader = DataLoader(dataset,batch_size=10,shuffle=True)
   