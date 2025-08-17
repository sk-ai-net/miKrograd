# UC-001.py
from  gt.core import Executable
import torch


@Executable('addition 2 vars')
def addition_1():
    a = torch.tensor(2.0, requires_grad=True).double()
    a.retain_grad()
    b = torch.tensor(4.0, requires_grad=True).double()
    b.retain_grad()
    c = a + b
    c.backward()

    return [a,b, torch.tensor(a.grad.item()).double(),  torch.tensor(b.grad.item()).double()], c

@Executable('addition var + double')
def addition_2():
    a = torch.tensor(2.0, requires_grad=True).double()
    a.retain_grad()
    c = a + 3.0
    c.backward()

    return [a], c
